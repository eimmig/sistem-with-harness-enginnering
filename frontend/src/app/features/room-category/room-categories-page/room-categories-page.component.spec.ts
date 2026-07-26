import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideEnvironmentNgxMask } from 'ngx-mask';
import { RoomCategoriesPageComponent } from './room-categories-page.component';
import { RoomCategory } from '../room-category.model';

describe('RoomCategoriesPageComponent', () => {
  let fixture: ComponentFixture<RoomCategoriesPageComponent>;
  let component: RoomCategoriesPageComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomCategoriesPageComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideEnvironmentNgxMask()]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomCategoriesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/room-categories').flush([]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates', () => {
    expect(component).toBeTruthy();
  });

  it('tracks the last created category', () => {
    const category: RoomCategory = { id: 1, name: 'Standard', prices: {} };

    component.onCategoryCreated(category);

    expect(component.lastCreatedCategory).toEqual(category);
  });
});
