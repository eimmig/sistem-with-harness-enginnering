import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { GuestsPageComponent } from './guests-page.component';
import { Guest } from '../guest.model';

describe('GuestsPageComponent', () => {
  let fixture: ComponentFixture<GuestsPageComponent>;
  let component: GuestsPageComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestsPageComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(GuestsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/guests').flush([]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates', () => {
    expect(component).toBeTruthy();
  });

  it('tracks the last created guest', () => {
    const guest: Guest = { id: 1, name: 'Maria Silva', document: '12345678900', phone: '11999998888' };

    component.onGuestCreated(guest);

    expect(component.lastCreatedGuest).toEqual(guest);
  });
});
