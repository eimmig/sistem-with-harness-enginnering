import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { GuestsInHotelComponent } from './guests-in-hotel.component';
import { Guest } from '../guest.model';

describe('GuestsInHotelComponent', () => {
  let fixture: ComponentFixture<GuestsInHotelComponent>;
  let component: GuestsInHotelComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestsInHotelComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(GuestsInHotelComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('loads the guests currently in the hotel', () => {
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);

    const req = httpMock.expectOne('/api/guests/in-hotel');
    expect(req.request.method).toBe('GET');

    const guests: Guest[] = [{ id: 1, name: 'Maria Silva', document: '12345678900', phone: '11999998888' }];
    req.flush(guests);

    expect(component.guests).toEqual(guests);
  });

  it('shows empty state when no guest is in the hotel', () => {
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/guests/in-hotel').flush([]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('[data-testid="guests-in-hotel-empty"]')).toBeTruthy();
  });
});
