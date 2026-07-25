import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RoomsPageComponent } from './rooms-page.component';
import { Room } from '../room.model';

describe('RoomsPageComponent', () => {
  let fixture: ComponentFixture<RoomsPageComponent>;
  let component: RoomsPageComponent;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomsPageComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/room-categories').flush([]);
    httpMock.expectOne('/api/rooms').flush([]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('creates', () => {
    expect(component).toBeTruthy();
  });

  it('tracks the last created room', () => {
    const room: Room = { id: 1, number: '101', status: 'AVAILABLE', category: { id: 1, name: 'Standard' } };

    component.onRoomCreated(room);

    expect(component.lastCreatedRoom).toEqual(room);
  });
});
