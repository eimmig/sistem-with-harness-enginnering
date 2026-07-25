import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { GuestFormComponent } from './guest-form.component';
import { Guest } from '../guest.model';

describe('GuestFormComponent', () => {
  let fixture: ComponentFixture<GuestFormComponent>;
  let component: GuestFormComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestFormComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(GuestFormComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates', () => {
    expect(component).toBeTruthy();
  });

  it('does not submit when the form is invalid', () => {
    component.submit();

    httpMock.expectNone('/api/guests');
    expect(component.form.controls.name.touched).toBeTrue();
  });

  it('creates a guest and emits guestCreated on success', () => {
    const created = jasmine.createSpy('guestCreated');
    component.guestCreated.subscribe(created);

    component.form.setValue({ name: 'Maria Silva', document: '12345678900', phone: '11999998888' });
    component.submit();

    const req = httpMock.expectOne('/api/guests');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ name: 'Maria Silva', document: '12345678900', phone: '11999998888' });

    const guest: Guest = { id: 1, name: 'Maria Silva', document: '12345678900', phone: '11999998888' };
    req.flush(guest);

    expect(created).toHaveBeenCalledWith(guest);
    expect(component.form.controls.name.value).toBe('');
  });

  it('shows an error message when the request fails', () => {
    component.form.setValue({ name: 'Maria Silva', document: '12345678900', phone: '11999998888' });
    component.submit();

    const req = httpMock.expectOne('/api/guests');
    req.flush({ message: 'error' }, { status: 400, statusText: 'Bad Request' });

    expect(component.errorMessage).toContain('Não foi possível cadastrar');
  });
});
