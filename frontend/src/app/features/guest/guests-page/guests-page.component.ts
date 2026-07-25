import { Component } from '@angular/core';
import { GuestFormComponent } from '../guest-form/guest-form.component';
import { GuestSearchComponent } from '../guest-search/guest-search.component';
import { Guest } from '../guest.model';

@Component({
  selector: 'app-guests-page',
  standalone: true,
  imports: [GuestFormComponent, GuestSearchComponent],
  templateUrl: './guests-page.component.html',
  styleUrl: './guests-page.component.scss'
})
export class GuestsPageComponent {
  lastCreatedGuest: Guest | null = null;

  onGuestCreated(guest: Guest): void {
    this.lastCreatedGuest = guest;
  }
}
