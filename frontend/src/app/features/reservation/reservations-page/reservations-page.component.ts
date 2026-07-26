import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { ReservationFormComponent } from '../reservation-form/reservation-form.component';
import { Reservation } from '../reservation.model';

@Component({
  selector: 'app-reservations-page',
  standalone: true,
  imports: [MatCardModule, ReservationFormComponent],
  templateUrl: './reservations-page.component.html',
  styleUrl: './reservations-page.component.scss'
})
export class ReservationsPageComponent {
  lastCreatedReservation: Reservation | null = null;

  onReservationCreated(reservation: Reservation): void {
    this.lastCreatedReservation = reservation;
  }
}
