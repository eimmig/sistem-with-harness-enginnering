import { test, expect } from '@playwright/test';

test.describe('Fluxo de hóspedes', () => {
  test('cadastra hóspede, busca por nome/documento/telefone e confere as listas de hóspedes', async ({ page }) => {
    const now = Date.now();
    const name = `Playwright Guest ${now}`;
    const document = now.toString().slice(-11);
    const phone = (now + 7).toString().slice(-11);

    await page.goto('/guests');

    await page.getByTestId('guest-name-input').fill(name);
    await page.getByTestId('guest-document-input').pressSequentially(document);
    await page.getByTestId('guest-phone-input').pressSequentially(phone);
    await page.getByTestId('guest-submit-button').click();

    const searchTable = page.getByTestId('guest-search-table');

    // A busca é atualizada automaticamente (refreshSignal) após o cadastro, sem filtro aplicado.
    await expect(searchTable).toContainText(name);

    // Busca por nome
    await page.getByTestId('search-name-input').fill(name);
    await page.getByTestId('search-submit-button').click();
    await expect(searchTable).toContainText(name);
    await expect(page.locator('[data-testid="guest-search-table"] tbody tr')).toHaveCount(1);

    // Busca por documento (limpa o filtro de nome)
    await page.getByTestId('search-name-input').fill('');
    await page.getByTestId('search-document-input').pressSequentially(document);
    await page.getByTestId('search-submit-button').click();
    await expect(searchTable).toContainText(name);

    // Busca por telefone (limpa o filtro de documento)
    await page.getByTestId('search-document-input').fill('');
    await page.getByTestId('search-phone-input').pressSequentially(phone);
    await page.getByTestId('search-submit-button').click();
    await expect(searchTable).toContainText(name);

    // Busca sem resultado
    await page.getByTestId('search-phone-input').fill('');
    await page.getByTestId('search-name-input').fill('Nome Inexistente Playwright ZZZ999');
    await page.getByTestId('search-submit-button').click();
    await expect(page.getByTestId('guest-search-empty')).toBeVisible();

    // Lista de hóspedes atualmente no hotel
    await page.goto('/guests-in-hotel');
    await expect(page.locator('h1')).toHaveText('Hóspedes no Hotel');
    await expect(
      page.getByTestId('guests-in-hotel-empty').or(page.getByTestId('guests-in-hotel-table'))
    ).toBeVisible();

    // Lista de hóspedes com reserva sem check-in
    await page.goto('/guests-without-check-in');
    await expect(page.locator('h1')).toHaveText('Hóspedes com Reserva sem Check-in');
    await expect(
      page.getByTestId('guests-without-checkin-empty').or(page.getByTestId('guests-without-checkin-table'))
    ).toBeVisible();
  });
});
