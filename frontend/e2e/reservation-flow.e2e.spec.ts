import { test, expect } from '@playwright/test';

const DAYS_OF_WEEK = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

function formatUsDate(date: Date): string {
  return `${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}`;
}

test.describe('Fluxo de reserva / check-in / check-out', () => {
  test('cria reserva, faz check-in (com aviso antes das 14h quando aplicavel) e faz check-out conferindo o detalhamento', async ({
    page
  }) => {
    const now = Date.now();
    const guestName = `Playwright Hospede Reserva ${now}`;
    const document = now.toString().slice(-11);
    const phone = (now + 7).toString().slice(-11);
    const categoryName = `Playwright Categoria Reserva ${now}`;
    const roomNumber = `PW-RES-${now}`;

    // Pre-requisitos: hospede, categoria de quarto com precos configurados, e o proprio quarto.
    await page.goto('/guests');
    await page.getByTestId('guest-name-input').fill(guestName);
    await page.getByTestId('guest-document-input').pressSequentially(document);
    await page.getByTestId('guest-phone-input').pressSequentially(phone);
    await page.getByTestId('guest-submit-button').click();
    await expect(page.getByTestId('guest-search-table')).toContainText(guestName);

    await page.goto('/room-categories');
    await page.getByTestId('category-name-input').fill(categoryName);
    await page.getByTestId('category-submit-button').click();
    // Clique forcado: mat-select vazio tem o mat-label sobreposto ao trigger (D-38 addendum).
    await page.getByTestId('category-select').click({ force: true });
    await page.getByRole('option', { name: categoryName }).click();
    for (const day of DAYS_OF_WEEK) {
      await page.locator(`[data-testid="price-input-${day}"]`).pressSequentially('120,00');
    }
    await page.getByTestId('prices-submit-button').click();
    await expect(page.getByTestId('prices-success')).toContainText('sucesso');

    await page.goto('/rooms');
    await page.getByTestId('room-number-input').fill(roomNumber);
    await page.getByTestId('room-category-select').click({ force: true });
    await page.getByRole('option', { name: categoryName }).click();
    await page.getByTestId('room-submit-button').click();
    await expect(
      page.locator('[data-testid="room-list-table"] tbody tr').filter({ hasText: roomNumber })
    ).toBeVisible();

    // Cria a reserva pela tela, buscando o hospede e selecionando o quarto criados acima.
    await page.goto('/reservations');
    await page.getByTestId('reservation-guest-query-input').fill(guestName);
    await page.getByTestId('reservation-guest-search-button').click();
    await page.getByRole('button', { name: guestName }).click();
    await expect(page.getByTestId('reservation-selected-guest')).toContainText(guestName);

    await page.getByTestId('reservation-room-select').click({ force: true });
    await page.getByRole('option', { name: roomNumber }).click();

    const today = new Date();
    const tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
    await page.getByTestId('reservation-checkin-date-input').fill(formatUsDate(today));
    await page.getByTestId('reservation-checkout-date-input').fill(formatUsDate(tomorrow));
    await page.getByTestId('reservation-parking-checkbox').click();

    await page.getByTestId('reservation-submit-button').click();
    await expect(page.getByTestId('reservation-created-message')).toContainText(guestName);

    // Check-in: o aviso de confirmacao (regra #6) so aparece se o horario real da maquina, no
    // momento do clique, for antes das 14h (D-21) -- por isso o teste trata os dois casos.
    await page.goto('/check-in');
    const checkInRow = page.locator('[data-testid="check-in-table"] tbody tr').filter({ hasText: guestName });
    await expect(checkInRow).toContainText(roomNumber);
    await checkInRow.getByRole('button', { name: 'Fazer check-in' }).click();

    const confirmButton = checkInRow.getByRole('button', { name: 'Confirmar' });
    const successMessage = page.getByTestId('check-in-success');
    // Espera a resposta assincrona do POST /check-in decidir qual dos dois estados aparece.
    await expect(confirmButton.or(successMessage)).toBeVisible();
    if (await confirmButton.isVisible()) {
      await confirmButton.click();
    }
    await expect(successMessage).toContainText(guestName);

    // Check-out: confere que o detalhamento completo (diarias + estacionamento + taxa de atraso +
    // total) e' exibido antes/junto da confirmacao (regra #8, D-22).
    await page.goto('/check-out');
    const checkOutRow = page.locator('[data-testid="check-out-table"] tbody tr').filter({ hasText: guestName });
    await expect(checkOutRow).toContainText(roomNumber);
    await checkOutRow.getByRole('button', { name: 'Fazer check-out' }).click();

    const result = page.locator('[data-testid="check-out-results"] li').filter({ hasText: guestName });
    await expect(result).toContainText('Diárias');
    await expect(result).toContainText('Estacionamento');
    await expect(result).toContainText('Taxa de atraso');
    await expect(result).toContainText('Total');
  });
});
