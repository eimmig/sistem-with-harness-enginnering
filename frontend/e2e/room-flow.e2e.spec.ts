import { test, expect } from '@playwright/test';
import { selectMatOption } from './mat-select-helper';

test.describe('Fluxo de quarto', () => {
  test('cadastra quarto vinculado a uma categoria e altera seu status pela lista de gestao', async ({ page }) => {
    const now = Date.now();
    const categoryName = `Playwright Categoria Quarto ${now}`;
    const roomNumber = `PW-${now}`;

    // Categoria precisa existir antes do quarto poder referencia-la.
    await page.goto('/room-categories');
    await page.getByTestId('category-name-input').fill(categoryName);
    await page.getByTestId('category-submit-button').click();

    await page.goto('/rooms');

    await page.getByTestId('room-number-input').fill(roomNumber);
    await selectMatOption(page, 'room-category-select', categoryName);
    await page.getByTestId('room-submit-button').click();

    const row = page.locator('[data-testid="room-list-table"] tbody tr').filter({ hasText: roomNumber });
    await expect(row).toContainText(categoryName);

    // Quarto recem-criado nasce com status Disponivel (D-17).
    await expect(row).toContainText('Disponível');

    const statusSelect = row.locator('mat-select');

    await statusSelect.click();
    await page.getByRole('option', { name: 'Sujo' }).click();
    await expect(row).toContainText('Sujo');

    await statusSelect.click();
    await page.getByRole('option', { name: 'Ocupado' }).click();
    await expect(row).toContainText('Ocupado');

    await statusSelect.click();
    await page.getByRole('option', { name: 'Disponível' }).click();
    await expect(row).toContainText('Disponível');
  });
});
