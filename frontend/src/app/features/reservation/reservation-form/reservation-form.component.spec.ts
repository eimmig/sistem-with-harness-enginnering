import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideNativeDateAdapter } from '@angular/material/core';
import { ReservationFormComponent } from './reservation-form.component';
import { Reservation } from '../reservation.model';
import { Guest } from '../../guest/guest.model';
import { Room } from '../../room/room.model';

describe('ReservationFormComponent', () => {
  let fixture: ComponentFixture<ReservationFormComponent>;
  let component: ReservationFormComponent;
  let httpMock: HttpTestingController;

  const rooms: Room[] = [{ id: 1, number: '101', status: 'AVAILABLE', category: { id: 1, name: 'Standard' } }];
  const guest: Guest = { id: 1, name: 'Maria Silva', document: '12345678900', phone: '11999998888' };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationFormComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideNativeDateAdapter()]
    }).compileComponents();

    fixture = TestBed.createComponent(ReservationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/rooms').flush(rooms);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates', () => {
    expect(component).toBeTruthy();
    expect(component.rooms.length).toBe(1);
  });

  it('searches and selects a guest', () => {
    component.guestQuery.setValue('Maria');
    component.searchGuests();

    const req = httpMock.expectOne((r) => r.url === '/api/guests' && r.params.get('name') === 'Maria');
    req.flush([guest]);

    expect(component.guestResults).toEqual([guest]);

    component.selectGuest(guest);

    expect(component.selectedGuest).toEqual(guest);
    expect(component.guestResults).toEqual([]);
  });

  it('does not submit without a selected guest', () => {
    component.form.setValue({
      roomId: 1,
      expectedCheckInDate: new Date(2026, 7, 3),
      expectedCheckInTime: '14:00',
      expectedCheckOutDate: new Date(2026, 7, 4),
      expectedCheckOutTime: '12:00',
      parkingRequested: false
    });

    component.submit();

    expect(component.errorMessage).toContain('Selecione um hóspede');
    httpMock.expectNone('/api/reservations');
  });

  it('does not submit when the form is invalid', () => {
    component.selectGuest(guest);

    component.submit();

    expect(component.form.controls.roomId.touched).toBeTrue();
    httpMock.expectNone('/api/reservations');
  });

  it('defaults check-in time to 14:00 and check-out time to 12:00', () => {
    expect(component.form.controls.expectedCheckInTime.value).toBe('14:00');
    expect(component.form.controls.expectedCheckOutTime.value).toBe('12:00');
  });

  it('creates a reservation and emits reservationCreated on success', () => {
    const created = jasmine.createSpy('reservationCreated');
    component.reservationCreated.subscribe(created);

    component.selectGuest(guest);
    component.form.setValue({
      roomId: 1,
      expectedCheckInDate: new Date(2026, 7, 3),
      expectedCheckInTime: '14:00',
      expectedCheckOutDate: new Date(2026, 7, 4),
      expectedCheckOutTime: '12:00',
      parkingRequested: true
    });

    component.submit();

    const req = httpMock.expectOne('/api/reservations');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      guestId: 1,
      roomId: 1,
      expectedCheckIn: '2026-08-03T14:00',
      expectedCheckOut: '2026-08-04T12:00',
      parkingRequested: true
    });

    const reservation: Reservation = {
      id: 1,
      guest: { id: 1, name: 'Maria Silva' },
      room: { id: 1, number: '101' },
      expectedCheckIn: '2026-08-03T14:00',
      expectedCheckOut: '2026-08-04T12:00',
      parkingRequested: true,
      actualCheckIn: null
    };
    req.flush(reservation);

    expect(created).toHaveBeenCalledWith(reservation);
    expect(component.selectedGuest).toBeNull();
  });

  it('shows an error message when the request fails', () => {
    component.selectGuest(guest);
    component.form.setValue({
      roomId: 1,
      expectedCheckInDate: new Date(2026, 7, 3),
      expectedCheckInTime: '14:00',
      expectedCheckOutDate: new Date(2026, 7, 4),
      expectedCheckOutTime: '12:00',
      parkingRequested: false
    });

    component.submit();

    const req = httpMock.expectOne('/api/reservations');
    req.flush({ message: 'error' }, { status: 400, statusText: 'Bad Request' });

    expect(component.errorMessage).toContain('Não foi possível criar a reserva');
  });
});
