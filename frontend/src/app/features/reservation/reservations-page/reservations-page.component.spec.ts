import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ReservationsPageComponent } from './reservations-page.component';
import { Reservation } from '../reservation.model';

describe('ReservationsPageComponent', () => {
  let fixture: ComponentFixture<ReservationsPageComponent>;
  let component: ReservationsPageComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationsPageComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(ReservationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/rooms').flush([]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates', () => {
    expect(component).toBeTruthy();
  });

  it('tracks the last created reservation', () => {
    const reservation: Reservation = {
      id: 1,
      guest: { id: 1, name: 'Maria Silva' },
      room: { id: 1, number: '101' },
      expectedCheckIn: '2026-08-03T14:00',
      expectedCheckOut: '2026-08-04T12:00',
      parkingRequested: false,
      actualCheckIn: null
    };

    component.onReservationCreated(reservation);

    expect(component.lastCreatedReservation).toEqual(reservation);
  });
});
