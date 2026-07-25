import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation, ReservationRequest } from './reservation.model';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/reservations';

  create(request: ReservationRequest): Observable<Reservation> {
    return this.http.post<Reservation>(this.baseUrl, request);
  }
}
