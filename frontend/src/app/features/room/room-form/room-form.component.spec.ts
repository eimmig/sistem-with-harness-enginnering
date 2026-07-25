import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RoomFormComponent } from './room-form.component';
import { Room } from '../room.model';
import { RoomCategory } from '../../room-category/room-category.model';

describe('RoomFormComponent', () => {
  let fixture: ComponentFixture<RoomFormComponent>;
  let component: RoomFormComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomFormComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);

    const categories: RoomCategory[] = [{ id: 1, name: 'Standard', prices: {} }];
    httpMock.expectOne('/api/room-categories').flush(categories);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates', () => {
    expect(component).toBeTruthy();
    expect(component.categories.length).toBe(1);
  });

  it('does not submit when the form is invalid', () => {
    component.submit();

    expect(component.form.controls.number.touched).toBeTrue();
    httpMock.expectNone('/api/rooms');
  });

  it('creates a room and emits roomCreated on success', () => {
    const created = jasmine.createSpy('roomCreated');
    component.roomCreated.subscribe(created);

    component.form.setValue({ number: '101', roomCategoryId: 1 });
    component.submit();

    const req = httpMock.expectOne('/api/rooms');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ number: '101', roomCategoryId: 1 });

    const room: Room = { id: 1, number: '101', status: 'AVAILABLE', category: { id: 1, name: 'Standard' } };
    req.flush(room);

    expect(created).toHaveBeenCalledWith(room);
  });

  it('shows an error message when the request fails', () => {
    component.form.setValue({ number: '101', roomCategoryId: 1 });
    component.submit();

    const req = httpMock.expectOne('/api/rooms');
    req.flush({ message: 'error' }, { status: 400, statusText: 'Bad Request' });

    expect(component.errorMessage).toContain('Não foi possível cadastrar');
  });
});
