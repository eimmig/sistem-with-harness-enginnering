import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'guests' },
  {
    path: 'guests',
    loadComponent: () => import('./features/guest/guests-page/guests-page.component').then((m) => m.GuestsPageComponent)
  },
  {
    path: 'room-categories',
    loadComponent: () =>
      import('./features/room-category/room-categories-page/room-categories-page.component').then(
        (m) => m.RoomCategoriesPageComponent
      )
  },
  {
    path: 'rooms',
    loadComponent: () => import('./features/room/rooms-page/rooms-page.component').then((m) => m.RoomsPageComponent)
  }
];
