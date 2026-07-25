import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RoomListComponent } from './room-list.component';
import { Room } from '../room.model';

describe('RoomListComponent', () => {
  let fixture: ComponentFixture<RoomListComponent>;
  let component: RoomListComponent;
  let httpMock: HttpTestingController;

  const rooms: Room[] = [{ id: 1, number: '101', status: 'AVAILABLE', category: { id: 1, name: 'Standard' } }];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomListComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('/api/rooms').flush(rooms);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('loads the rooms on init', () => {
    expect(component.rooms).toEqual(rooms);
  });

  it('changes the room status', () => {
    const updated: Room = { ...rooms[0], status: 'DIRTY' };

    component.changeStatus(component.rooms[0], 'DIRTY');

    const req = httpMock.expectOne('/api/rooms/1/status');
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ status: 'DIRTY' });
    req.flush(updated);

    expect(component.rooms[0].status).toBe('DIRTY');
  });

  it('shows empty state when there are no rooms', () => {
    component.rooms = [];
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('[data-testid="room-list-empty"]')).toBeTruthy();
  });
});
