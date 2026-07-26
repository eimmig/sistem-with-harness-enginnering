import { Page } from '@playwright/test';

/**
 * Abre um mat-select (clique forcado, ja que o mat-label de um select vazio sobrepoe visualmente
 * o trigger -- D-38 addendum) e seleciona a opcao pelo texto. Ocasionalmente o clique nao abre o
 * painel de opcoes (flakiness conhecida do Angular Material com click({force:true}) em headless);
 * tenta de novo algumas vezes antes de desistir.
 */
export async function selectMatOption(page: Page, selectTestId: string, optionName: string): Promise<void> {
  const maxAttempts = 4;
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    await page.getByTestId(selectTestId).click({ force: true });
    const option = page.getByRole('option', { name: optionName });
    try {
      await option.waitFor({ state: 'visible', timeout: 5000 });
      await option.click();
      return;
    } catch (error) {
      await page.keyboard.press('Escape');
      if (attempt === maxAttempts) {
        throw error;
      }
    }
  }
}
