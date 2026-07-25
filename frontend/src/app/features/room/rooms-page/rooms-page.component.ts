import { Component } from '@angular/core';
import { RoomFormComponent } from '../room-form/room-form.component';
import { RoomListComponent } from '../room-list/room-list.component';
import { Room } from '../room.model';

@Component({
  selector: 'app-rooms-page',
  standalone: true,
  imports: [RoomFormComponent, RoomListComponent],
  templateUrl: './rooms-page.component.html',
  styleUrl: './rooms-page.component.scss'
})
export class RoomsPageComponent {
  lastCreatedRoom: Room | null = null;

  onRoomCreated(room: Room): void {
    this.lastCreatedRoom = room;
  }
}
