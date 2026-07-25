import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'guests' },
  {
    path: 'guests',
    loadComponent: () => import('./features/guest/guests-page/guests-page.component').then((m) => m.GuestsPageComponent)
  }
];
