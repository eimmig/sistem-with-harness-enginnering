import { Component, OnInit, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { GuestService } from '../guest.service';
import { Guest } from '../guest.model';

@Component({
  selector: 'app-guests-in-hotel',
  standalone: true,
  imports: [MatCardModule, MatTableModule],
  templateUrl: './guests-in-hotel.component.html',
  styleUrl: './guests-in-hotel.component.scss'
})
export class GuestsInHotelComponent implements OnInit {
  private readonly guestService = inject(GuestService);

  readonly displayedColumns = ['name', 'document', 'phone'];
  guests: Guest[] = [];

  ngOnInit(): void {
    this.guestService.guestsInHotel().subscribe((guests) => {
      this.guests = guests;
    });
  }
}
