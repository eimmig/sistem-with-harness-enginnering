import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoomCategory, RoomCategoryPricesRequest, RoomCategoryRequest } from './room-category.model';

@Injectable({ providedIn: 'root' })
export class RoomCategoryService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/room-categories';

  list(): Observable<RoomCategory[]> {
    return this.http.get<RoomCategory[]>(this.baseUrl);
  }

  create(request: RoomCategoryRequest): Observable<RoomCategory> {
    return this.http.post<RoomCategory>(this.baseUrl, request);
  }

  updatePrices(id: number, request: RoomCategoryPricesRequest): Observable<RoomCategory> {
    return this.http.put<RoomCategory>(`${this.baseUrl}/${id}/prices`, request);
  }
}
