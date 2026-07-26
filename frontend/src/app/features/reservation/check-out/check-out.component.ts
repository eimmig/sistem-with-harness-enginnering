import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { ReservationService } from '../reservation.service';
import { CheckOutResult, Reservation } from '../reservation.model';

interface CompletedCheckOut {
  reservation: Reservation;
  result: CheckOutResult;
}

@Component({
  selector: 'app-check-out',
  standalone: true,
  imports: [MatCardModule, MatTableModule, MatButtonModule],
  templateUrl: './check-out.component.html',
  styleUrl: './check-out.component.scss'
})
export class CheckOutComponent implements OnInit {
  private readonly reservationService = inject(ReservationService);

  readonly displayedColumns = ['guest', 'room', 'actions'];

  reservations: Reservation[] = [];
  completedCheckOuts: CompletedCheckOut[] = [];
  errorMessages: Record<number, string> = {};

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.reservationService.pendingCheckOut().subscribe((reservations) => {
      this.reservations = reservations;
    });
  }

  performCheckOut(reservation: Reservation): void {
    delete this.errorMessages[reservation.id];

    this.reservationService.checkOut(reservation.id).subscribe({
      next: (result) => {
        this.completedCheckOuts = [{ reservation, result }, ...this.completedCheckOuts];
        this.reservations = this.reservations.filter((r) => r.id !== reservation.id);
      },
      error: (response: HttpErrorResponse) => {
        this.errorMessages[reservation.id] =
          response.status === 409
            ? 'A reserva ainda não teve check-in ou já teve check-out.'
            : 'Não foi possível realizar o check-out.';
      }
    });
  }
}
