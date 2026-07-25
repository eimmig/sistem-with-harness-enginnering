import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RoomCategoryFormComponent } from './room-category-form.component';
import { RoomCategory } from '../room-category.model';

describe('RoomCategoryFormComponent', () => {
  let fixture: ComponentFixture<RoomCategoryFormComponent>;
  let component: RoomCategoryFormComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomCategoryFormComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomCategoryFormComponent);
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

    expect(component.form.controls.name.touched).toBeTrue();
    httpMock.expectNone('/api/room-categories');
  });

  it('creates a category and emits categoryCreated on success', () => {
    const created = jasmine.createSpy('categoryCreated');
    component.categoryCreated.subscribe(created);

    component.form.setValue({ name: 'Standard' });
    component.submit();

    const req = httpMock.expectOne('/api/room-categories');
    expect(req.request.method).toBe('POST');

    const category: RoomCategory = { id: 1, name: 'Standard', prices: {} };
    req.flush(category);

    expect(created).toHaveBeenCalledWith(category);
  });
});
