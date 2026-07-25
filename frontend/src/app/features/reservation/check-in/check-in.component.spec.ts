import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CheckInComponent } from './check-in.component';
import { Reservation } from '../reservation.model';

describe('CheckInComponent', () => {
  let fixture: ComponentFixture<CheckInComponent>;
  let component: CheckInComponent;
  let httpMock: HttpTestingController;

  const reservation: Reservation = {
    id: 1,
    guest: { id: 1, name: 'Maria Silva' },
    room: { id: 1, number: '101' },
    expectedCheckIn: '2026-08-03T14:00',
    expectedCheckOut: '2026-08-04T12:00',
    parkingRequested: false,
    actualCheckIn: null
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckInComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(CheckInComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/reservations/pending-check-in').flush([reservation]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('loads reservations pending check-in', () => {
    expect(component.reservations).toEqual([reservation]);
  });

  it('checks in successfully and removes the reservation from the list', () => {
    component.requestCheckIn(reservation);

    const req = httpMock.expectOne('/api/reservations/1/check-in');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ confirmedByAttendant: false });

    req.flush({ ...reservation, actualCheckIn: '2026-08-03T15:00' });

    expect(component.reservations).toEqual([]);
    expect(component.successMessage).toContain('Maria Silva');
  });

  it('asks for confirmation when check-in is before 2pm', () => {
    component.requestCheckIn(reservation);

    const req = httpMock.expectOne('/api/reservations/1/check-in');
    req.flush({ message: 'requires confirmation' }, { status: 400, statusText: 'Bad Request' });

    expect(component.confirmationNeededFor).toBe(1);
  });

  it('confirms check-in after the warning', () => {
    component.confirmationNeededFor = 1;

    component.confirmCheckIn(reservation);

    const req = httpMock.expectOne('/api/reservations/1/check-in');
    expect(req.request.body).toEqual({ confirmedByAttendant: true });
    req.flush({ ...reservation, actualCheckIn: '2026-08-03T09:00' });

    expect(component.confirmationNeededFor).toBeNull();
    expect(component.reservations).toEqual([]);
  });

  it('shows an error message when the room is unavailable', () => {
    component.requestCheckIn(reservation);

    const req = httpMock.expectOne('/api/reservations/1/check-in');
    req.flush({ message: 'conflict' }, { status: 409, statusText: 'Conflict' });

    expect(component.errorMessages[1]).toContain('indisponível');
  });
});
