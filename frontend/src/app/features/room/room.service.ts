import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Room, RoomRequest, RoomStatus } from './room.model';

@Injectable({ providedIn: 'root' })
export class RoomService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/rooms';

  list(): Observable<Room[]> {
    return this.http.get<Room[]>(this.baseUrl);
  }

  create(request: RoomRequest): Observable<Room> {
    return this.http.post<Room>(this.baseUrl, request);
  }

  updateStatus(id: number, status: RoomStatus): Observable<Room> {
    return this.http.patch<Room>(`${this.baseUrl}/${id}/status`, { status });
  }
}
