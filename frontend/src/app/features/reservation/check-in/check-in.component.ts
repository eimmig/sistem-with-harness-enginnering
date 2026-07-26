import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { ReservationService } from '../reservation.service';
import { Reservation } from '../reservation.model';

@Component({
  selector: 'app-check-in',
  standalone: true,
  imports: [MatCardModule, MatTableModule, MatButtonModule],
  templateUrl: './check-in.component.html',
  styleUrl: './check-in.component.scss'
})
export class CheckInComponent implements OnInit {
  private readonly reservationService = inject(ReservationService);

  readonly displayedColumns = ['guest', 'room', 'expectedCheckIn', 'actions'];

  reservations: Reservation[] = [];
  confirmationNeededFor: number | null = null;
  errorMessages: Record<number, string> = {};
  successMessage: string | null = null;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.reservationService.pendingCheckIn().subscribe((reservations) => {
      this.reservations = reservations;
    });
  }

  requestCheckIn(reservation: Reservation): void {
    this.performCheckIn(reservation, false);
  }

  confirmCheckIn(reservation: Reservation): void {
    this.performCheckIn(reservation, true);
  }

  cancelConfirmation(): void {
    this.confirmationNeededFor = null;
  }

  private performCheckIn(reservation: Reservation, confirmedByAttendant: boolean): void {
    delete this.errorMessages[reservation.id];
    this.successMessage = null;

    this.reservationService.checkIn(reservation.id, confirmedByAttendant).subscribe({
      next: () => {
        this.confirmationNeededFor = null;
        this.successMessage = `Check-in realizado para ${reservation.guest.name}.`;
        this.reservations = this.reservations.filter((r) => r.id !== reservation.id);
      },
      error: (response: HttpErrorResponse) => {
        if (response.status === 400) {
          this.confirmationNeededFor = reservation.id;
        } else {
          this.confirmationNeededFor = null;
          this.errorMessages[reservation.id] =
            response.status === 409
              ? 'Quarto indisponível para check-in ou hóspede já com check-in feito.'
              : 'Não foi possível realizar o check-in.';
        }
      }
    });
  }
}
