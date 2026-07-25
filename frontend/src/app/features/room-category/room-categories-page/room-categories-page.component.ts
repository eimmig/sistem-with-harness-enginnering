import { Component } from '@angular/core';
import { RoomCategoryFormComponent } from '../room-category-form/room-category-form.component';
import { RoomCategoryPriceComponent } from '../room-category-price/room-category-price.component';
import { RoomCategory } from '../room-category.model';

@Component({
  selector: 'app-room-categories-page',
  standalone: true,
  imports: [RoomCategoryFormComponent, RoomCategoryPriceComponent],
  templateUrl: './room-categories-page.component.html',
  styleUrl: './room-categories-page.component.scss'
})
export class RoomCategoriesPageComponent {
  lastCreatedCategory: RoomCategory | null = null;

  onCategoryCreated(category: RoomCategory): void {
    this.lastCreatedCategory = category;
  }
}
