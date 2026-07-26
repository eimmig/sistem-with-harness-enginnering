import { Component, Input, OnChanges, OnInit, SimpleChanges, inject } from '@angular/core';
import { NgClass } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { RoomService } from '../room.service';
import { ROOM_STATUSES, ROOM_STATUS_LABELS, Room, RoomStatus } from '../room.model';

@Component({
  selector: 'app-room-list',
  standalone: true,
  imports: [MatTableModule, MatSelectModule, NgClass],
  templateUrl: './room-list.component.html',
  styleUrl: './room-list.component.scss'
})
export class RoomListComponent implements OnInit, OnChanges {
  private readonly roomService = inject(RoomService);

  @Input() refreshSignal: unknown;

  readonly displayedColumns = ['number', 'category', 'status'];
  readonly statuses = ROOM_STATUSES;
  readonly statusLabels = ROOM_STATUS_LABELS;

  rooms: Room[] = [];

  ngOnInit(): void {
    this.load();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['refreshSignal']?.firstChange) {
      this.load();
    }
  }

  load(): void {
    this.roomService.list().subscribe((rooms) => {
      this.rooms = rooms;
    });
  }

  changeStatus(room: Room, status: RoomStatus): void {
    this.roomService.updateStatus(room.id, status).subscribe((updated) => {
      room.status = updated.status;
    });
  }

  statusLabel(status: RoomStatus): string {
    return this.statusLabels[status];
  }

  statusClass(status: RoomStatus): string {
    return status.toLowerCase();
  }
}
