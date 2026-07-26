import { Component, EventEmitter, OnInit, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ReservationService } from '../reservation.service';
import { Reservation } from '../reservation.model';
import { GuestService } from '../../guest/guest.service';
import { Guest } from '../../guest/guest.model';
import { RoomService } from '../../room/room.service';
import { Room } from '../../room/room.model';

const STANDARD_CHECK_IN_TIME = '14:00';
const STANDARD_CHECK_OUT_TIME = '12:00';

@Component({
  selector: 'app-reservation-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatButtonModule,
    MatDatepickerModule
  ],
  templateUrl: './reservation-form.component.html',
  styleUrl: './reservation-form.component.scss'
})
export class ReservationFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly reservationService = inject(ReservationService);
  private readonly guestService = inject(GuestService);
  private readonly roomService = inject(RoomService);

  @Output() reservationCreated = new EventEmitter<Reservation>();

  rooms: Room[] = [];
  guestResults: Guest[] = [];
  selectedGuest: Guest | null = null;
  errorMessage: string | null = null;

  guestQuery = this.formBuilder.nonNullable.control('');

  form = this.formBuilder.group({
    roomId: this.formBuilder.control<number | null>(null, Validators.required),
    expectedCheckInDate: this.formBuilder.control<Date | null>(null, Validators.required),
    expectedCheckInTime: this.formBuilder.nonNullable.control(STANDARD_CHECK_IN_TIME, Validators.required),
    expectedCheckOutDate: this.formBuilder.control<Date | null>(null, Validators.required),
    expectedCheckOutTime: this.formBuilder.nonNullable.control(STANDARD_CHECK_OUT_TIME, Validators.required),
    parkingRequested: this.formBuilder.nonNullable.control(false)
  });

  ngOnInit(): void {
    this.roomService.list().subscribe((rooms) => {
      this.rooms = rooms;
    });
  }

  searchGuests(): void {
    this.guestService.search({ name: this.guestQuery.value }).subscribe((guests) => {
      this.guestResults = guests;
    });
  }

  selectGuest(guest: Guest): void {
    this.selectedGuest = guest;
    this.guestResults = [];
    this.guestQuery.setValue(guest.name);
  }

  submit(): void {
    if (this.selectedGuest === null || this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMessage = this.selectedGuest === null ? 'Selecione um hóspede.' : null;
      return;
    }

    this.errorMessage = null;
    const { roomId, expectedCheckInDate, expectedCheckInTime, expectedCheckOutDate, expectedCheckOutTime, parkingRequested } =
      this.form.getRawValue();
    this.reservationService
      .create({
        guestId: this.selectedGuest.id,
        roomId: roomId as number,
        expectedCheckIn: combineDateAndTime(expectedCheckInDate as Date, expectedCheckInTime),
        expectedCheckOut: combineDateAndTime(expectedCheckOutDate as Date, expectedCheckOutTime),
        parkingRequested
      })
      .subscribe({
        next: (reservation) => {
          this.reservationCreated.emit(reservation);
          this.form.reset({
            parkingRequested: false,
            expectedCheckInTime: STANDARD_CHECK_IN_TIME,
            expectedCheckOutTime: STANDARD_CHECK_OUT_TIME
          });
          this.guestQuery.reset('');
          this.selectedGuest = null;
        },
        error: () => {
          this.errorMessage = 'Não foi possível criar a reserva. Verifique os dados e tente novamente.';
        }
      });
  }
}

/** Combines a calendar date with an "HH:mm" time into the "yyyy-MM-ddTHH:mm" format the backend expects. */
function combineDateAndTime(date: Date, time: string): string {
  const [hours, minutes] = time.split(':');
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}T${hours.padStart(2, '0')}:${minutes.padStart(2, '0')}`;
}
