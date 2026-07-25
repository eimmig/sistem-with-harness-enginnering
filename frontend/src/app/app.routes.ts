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
  },
  {
    path: 'reservations',
    loadComponent: () =>
      import('./features/reservation/reservations-page/reservations-page.component').then(
        (m) => m.ReservationsPageComponent
      )
  },
  {
    path: 'check-in',
    loadComponent: () => import('./features/reservation/check-in/check-in.component').then((m) => m.CheckInComponent)
  },
  {
    path: 'check-out',
    loadComponent: () =>
      import('./features/reservation/check-out/check-out.component').then((m) => m.CheckOutComponent)
  },
  {
    path: 'guests-in-hotel',
    loadComponent: () =>
      import('./features/guest/guests-in-hotel/guests-in-hotel.component').then((m) => m.GuestsInHotelComponent)
  }
];
