import { test, expect } from '@playwright/test';
import { selectMatOption } from './mat-select-helper';

const WEEKDAY_TESTIDS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
const WEEKEND_TESTIDS = ['SATURDAY', 'SUNDAY'];

test.describe('Fluxo de categoria de quarto', () => {
  test('cadastra categoria, configura preco por dia da semana e confere persistencia ao reabrir a tela', async ({
    page
  }) => {
    const categoryName = `Playwright Categoria ${Date.now()}`;

    await page.goto('/room-categories');

    await page.getByTestId('category-name-input').fill(categoryName);
    await page.getByTestId('category-submit-button').click();

    // O select de categorias e' atualizado automaticamente (refreshSignal) apos o cadastro.
    await selectMatOption(page, 'category-select', categoryName);

    for (const day of WEEKDAY_TESTIDS) {
      await page.locator(`[data-testid="price-input-${day}"]`).pressSequentially('120,00');
    }
    for (const day of WEEKEND_TESTIDS) {
      await page.locator(`[data-testid="price-input-${day}"]`).pressSequentially('150,00');
    }

    await page.getByTestId('prices-submit-button').click();
    await expect(page.getByTestId('prices-success')).toContainText('sucesso');

    // Reabre a tela (reload) e confere que os valores salvos aparecem ao reselecionar a categoria.
    await page.reload();
    await page.waitForLoadState('networkidle');
    await selectMatOption(page, 'category-select', categoryName);

    // Ao reabrir, o valor pode ser reexibido sem forcar 2 casas decimais (ex.: "R$ 120" em vez de
    // "R$ 120,00") -- o que importa e' o numero persistido, nao a formatacao exata de redisplay.
    for (const day of WEEKDAY_TESTIDS) {
      await expect(page.locator(`[data-testid="price-input-${day}"]`)).toHaveValue(/^R\$ 120(,00)?$/);
    }
    for (const day of WEEKEND_TESTIDS) {
      await expect(page.locator(`[data-testid="price-input-${day}"]`)).toHaveValue(/^R\$ 150(,00)?$/);
    }
  });
});
