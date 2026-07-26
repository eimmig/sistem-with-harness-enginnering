import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideEnvironmentNgxMask } from 'ngx-mask';
import { GuestSearchComponent } from './guest-search.component';
import { Guest } from '../guest.model';

describe('GuestSearchComponent', () => {
  let fixture: ComponentFixture<GuestSearchComponent>;
  let component: GuestSearchComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestSearchComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideEnvironmentNgxMask()]
    }).compileComponents();

    fixture = TestBed.createComponent(GuestSearchComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('loads all guests on init', () => {
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);

    const req = httpMock.expectOne('/api/guests');
    expect(req.request.method).toBe('GET');

    const guests: Guest[] = [{ id: 1, name: 'Maria Silva', document: '12345678900', phone: '11999998888' }];
    req.flush(guests);

    expect(component.guests).toEqual(guests);
    expect(component.searched).toBeTrue();
  });

  it('searches with the filled filters', () => {
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    httpMock.expectOne('/api/guests').flush([]);

    component.form.setValue({ name: 'Maria', document: '', phone: '' });
    component.search();

    const req = httpMock.expectOne((r) => r.url === '/api/guests' && r.params.get('name') === 'Maria');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('shows empty state when no guest matches', () => {
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
    httpMock.expectOne('/api/guests').flush([]);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('[data-testid="guest-search-empty"]')).toBeTruthy();
  });
});
