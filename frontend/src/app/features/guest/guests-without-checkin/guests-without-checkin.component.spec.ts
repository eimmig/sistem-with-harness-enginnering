import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { GuestsWithoutCheckinComponent } from './guests-without-checkin.component';
import { Guest } from '../guest.model';

describe('GuestsWithoutCheckinComponent', () => {
  let fixture: ComponentFixture<GuestsWithoutCheckinComponent>;
  let component: GuestsWithoutCheckinComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestsWithoutCheckinComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(GuestsWithoutCheckinComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('loads the guests with a reservation pending check-in', () => {
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);

    const req = httpMock.expectOne('/api/guests/without-check-in');
    expect(req.request.method).toBe('GET');

    const guests: Guest[] = [{ id: 1, name: 'Maria Silva', document: '12345678900', phone: '11999998888' }];
    req.flush(guests);

    expect(component.guests).toEqual(guests);
  });

  it('shows empty state when no guest is pending check-in', () => {
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/guests/without-check-in').flush([]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('[data-testid="guests-without-checkin-empty"]')).toBeTruthy();
  });
});
