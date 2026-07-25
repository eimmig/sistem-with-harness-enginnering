import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CheckOutComponent } from './check-out.component';
import { CheckOutResult, Reservation } from '../reservation.model';

describe('CheckOutComponent', () => {
  let fixture: ComponentFixture<CheckOutComponent>;
  let component: CheckOutComponent;
  let httpMock: HttpTestingController;

  const reservation: Reservation = {
    id: 1,
    guest: { id: 1, name: 'Maria Silva' },
    room: { id: 1, number: '101' },
    expectedCheckIn: '2026-08-03T14:00',
    expectedCheckOut: '2026-08-04T12:00',
    parkingRequested: false,
    actualCheckIn: '2026-08-03T14:00'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckOutComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(CheckOutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/reservations/pending-check-out').flush([reservation]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('loads reservations pending check-out', () => {
    expect(component.reservations).toEqual([reservation]);
  });

  it('checks out and shows the full breakdown', () => {
    component.performCheckOut(reservation);

    const req = httpMock.expectOne('/api/reservations/1/check-out');
    expect(req.request.method).toBe('POST');

    const result: CheckOutResult = {
      reservationId: 1,
      dailyRateTotal: 120,
      parkingFeeTotal: 0,
      lateCheckOutFee: 0,
      total: 120,
      actualCheckOut: '2026-08-04T11:00'
    };
    req.flush(result);

    expect(component.reservations).toEqual([]);
    expect(component.completedCheckOuts).toEqual([{ reservation, result }]);
  });

  it('shows an error message when check-out is not allowed', () => {
    component.performCheckOut(reservation);

    const req = httpMock.expectOne('/api/reservations/1/check-out');
    req.flush({ message: 'conflict' }, { status: 409, statusText: 'Conflict' });

    expect(component.errorMessages[1]).toContain('check-in');
  });
});
