import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { RoomFormComponent } from '../room-form/room-form.component';
import { RoomListComponent } from '../room-list/room-list.component';
import { Room } from '../room.model';

@Component({
  selector: 'app-rooms-page',
  standalone: true,
  imports: [MatCardModule, RoomFormComponent, RoomListComponent],
  templateUrl: './rooms-page.component.html',
  styleUrl: './rooms-page.component.scss'
})
export class RoomsPageComponent {
  lastCreatedRoom: Room | null = null;

  onRoomCreated(room: Room): void {
    this.lastCreatedRoom = room;
  }
}
