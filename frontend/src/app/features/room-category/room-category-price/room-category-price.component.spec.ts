import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideEnvironmentNgxMask } from 'ngx-mask';
import { RoomCategoryPriceComponent } from './room-category-price.component';
import { RoomCategory } from '../room-category.model';

describe('RoomCategoryPriceComponent', () => {
  let fixture: ComponentFixture<RoomCategoryPriceComponent>;
  let component: RoomCategoryPriceComponent;
  let httpMock: HttpTestingController;

  const categories: RoomCategory[] = [
    { id: 1, name: 'Standard', prices: { MONDAY: 120, TUESDAY: 120, WEDNESDAY: 120, THURSDAY: 120, FRIDAY: 120, SATURDAY: 150, SUNDAY: 150 } }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomCategoryPriceComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideEnvironmentNgxMask()]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomCategoryPriceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/room-categories').flush(categories);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates', () => {
    expect(component).toBeTruthy();
  });

  it('loads the existing prices when a category is selected', () => {
    component.onCategorySelected(1);

    expect(component.form.controls.MONDAY.value).toBe(120);
    expect(component.form.controls.SATURDAY.value).toBe(150);
  });

  it('does not submit when the price form is invalid', () => {
    component.onCategorySelected(1);
    component.form.controls.MONDAY.setValue(null);

    component.submit();

    expect(component.form.controls.MONDAY.touched).toBeTrue();
    httpMock.expectNone('/api/room-categories/1/prices');
  });

  it('submits the 7 day prices and shows a success message', () => {
    component.onCategorySelected(1);
    component.submit();

    const req = httpMock.expectOne('/api/room-categories/1/prices');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body.prices.MONDAY).toBe(120);
    expect(req.request.body.prices.SUNDAY).toBe(150);

    req.flush({ ...categories[0] });
    httpMock.expectOne('/api/room-categories').flush(categories);

    expect(component.successMessage).toContain('sucesso');
  });

  it('shows an error message when the update fails', () => {
    component.onCategorySelected(1);
    component.submit();

    const req = httpMock.expectOne('/api/room-categories/1/prices');
    req.flush({ message: 'error' }, { status: 400, statusText: 'Bad Request' });

    expect(component.errorMessage).toContain('Não foi possível atualizar');
  });
});
