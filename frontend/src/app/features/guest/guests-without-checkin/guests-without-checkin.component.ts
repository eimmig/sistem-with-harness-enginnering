import { Component, OnInit, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { GuestService } from '../guest.service';
import { Guest } from '../guest.model';

@Component({
  selector: 'app-guests-without-checkin',
  standalone: true,
  imports: [MatCardModule, MatTableModule],
  templateUrl: './guests-without-checkin.component.html',
  styleUrl: './guests-without-checkin.component.scss'
})
export class GuestsWithoutCheckinComponent implements OnInit {
  private readonly guestService = inject(GuestService);

  readonly displayedColumns = ['name', 'document', 'phone'];
  guests: Guest[] = [];

  ngOnInit(): void {
    this.guestService.guestsWithoutCheckIn().subscribe((guests) => {
      this.guests = guests;
    });
  }
}
