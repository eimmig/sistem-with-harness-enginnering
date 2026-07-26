import { PostgreSqlContainer, StartedPostgreSqlContainer } from '@testcontainers/postgresql';
import { ChildProcess, execSync, spawn } from 'node:child_process';
import net from 'node:net';
import path from 'node:path';

const BACKEND_PORT = 8080;
const BACKEND_READY_URL = `http://localhost:${BACKEND_PORT}/api/guests`;
const BACKEND_STARTUP_TIMEOUT_MS = 120_000;

/** Garante que nenhum outro processo (ex.: backend rodando manualmente) ja ocupa a porta. */
async function assertPortFree(port: number): Promise<void> {
  await new Promise<void>((resolve, reject) => {
    const tester = net
      .createServer()
      .once('error', (error: NodeJS.ErrnoException) => {
        if (error.code === 'EADDRINUSE') {
          reject(
            new Error(
              `Port ${port} is already in use. Stop any backend running manually (e.g. 'mvnw spring-boot:run') ` +
                'before running the Playwright suite -- the E2E backend needs this port for its own ephemeral database.'
            )
          );
        } else {
          reject(error);
        }
      })
      .once('listening', () => tester.close(() => resolve()))
      .listen(port, '127.0.0.1');
  });
}

async function waitForBackend(url: string, timeoutMs: number): Promise<void> {
  const deadline = Date.now() + timeoutMs;
  while (Date.now() < deadline) {
    try {
      const response = await fetch(url);
      if (response.status < 500) {
        return;
      }
    } catch {
      // backend ainda nao esta de pe -- tenta de novo ate o timeout
    }
    await new Promise((resolve) => setTimeout(resolve, 1000));
  }
  throw new Error(`Backend did not become ready at ${url} within ${timeoutMs}ms`);
}

/** No Windows, matar so o processo do 'mvnw.cmd' deixa o java.exe filho vivo -- precisa matar a arvore toda. */
function killProcessTree(pid: number): void {
  if (process.platform === 'win32') {
    try {
      execSync(`taskkill /pid ${pid} /T /F`);
    } catch {
      // processo pode ja ter terminado sozinho
    }
  } else {
    try {
      process.kill(-pid, 'SIGKILL');
    } catch {
      // processo pode ja ter terminado sozinho
    }
  }
}

/**
 * Sobe um Postgres efemero (Testcontainers) dedicado a esta execucao da suite Playwright, e o
 * backend real apontando para ele -- garante que os dados de teste nunca poluam o Postgres do
 * docker-compose usado para testar a aplicacao manualmente (D-38/D-42).
 */
export default async function globalSetup(): Promise<() => Promise<void>> {
  await assertPortFree(BACKEND_PORT);

  const container: StartedPostgreSqlContainer = await new PostgreSqlContainer('postgres:16-alpine')
    .withDatabase('gestao_hospedes')
    .withUsername('postgres')
    .withPassword('postgres')
    .start();

  const dbUrl = `jdbc:postgresql://${container.getHost()}:${container.getPort()}/${container.getDatabase()}`;

  const backendProcess: ChildProcess = spawn('.\\mvnw.cmd spring-boot:run', {
    cwd: path.join(__dirname, '..', '..', 'backend'),
    shell: true,
    env: {
      ...process.env,
      DB_URL: dbUrl,
      DB_USER: container.getUsername(),
      DB_PASSWORD: container.getPassword()
    }
  });

  backendProcess.on('error', (error) => {
    console.error('[e2e] Failed to start backend process:', error);
  });

  try {
    await waitForBackend(BACKEND_READY_URL, BACKEND_STARTUP_TIMEOUT_MS);
  } catch (error) {
    if (backendProcess.pid) {
      killProcessTree(backendProcess.pid);
    }
    await container.stop();
    throw error;
  }

  return async () => {
    if (backendProcess.pid) {
      killProcessTree(backendProcess.pid);
    }
    await container.stop();
  };
}
