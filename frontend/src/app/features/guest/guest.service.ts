import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Guest, GuestRequest, GuestSearchFilter } from './guest.model';

@Injectable({ providedIn: 'root' })
export class GuestService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/guests';

  create(request: GuestRequest): Observable<Guest> {
    return this.http.post<Guest>(this.baseUrl, request);
  }

  search(filter: GuestSearchFilter): Observable<Guest[]> {
    let params = new HttpParams();
    if (filter.name) {
      params = params.set('name', filter.name);
    }
    if (filter.document) {
      params = params.set('document', filter.document);
    }
    if (filter.phone) {
      params = params.set('phone', filter.phone);
    }
    return this.http.get<Guest[]>(this.baseUrl, { params });
  }

  guestsInHotel(): Observable<Guest[]> {
    return this.http.get<Guest[]>(`${this.baseUrl}/in-hotel`);
  }
}
