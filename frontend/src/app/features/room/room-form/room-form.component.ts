import { Component, EventEmitter, OnInit, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { RoomService } from '../room.service';
import { Room } from '../room.model';
import { RoomCategoryService } from '../../room-category/room-category.service';
import { RoomCategory } from '../../room-category/room-category.model';

@Component({
  selector: 'app-room-form',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule],
  templateUrl: './room-form.component.html',
  styleUrl: './room-form.component.scss'
})
export class RoomFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly roomService = inject(RoomService);
  private readonly roomCategoryService = inject(RoomCategoryService);

  @Output() roomCreated = new EventEmitter<Room>();

  categories: RoomCategory[] = [];
  errorMessage: string | null = null;

  form = this.formBuilder.group({
    number: this.formBuilder.nonNullable.control('', Validators.required),
    roomCategoryId: this.formBuilder.control<number | null>(null, Validators.required)
  });

  ngOnInit(): void {
    this.roomCategoryService.list().subscribe((categories) => {
      this.categories = categories;
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = null;
    const { number, roomCategoryId } = this.form.getRawValue();
    this.roomService.create({ number, roomCategoryId: roomCategoryId as number }).subscribe({
      next: (room) => {
        this.roomCreated.emit(room);
        this.form.reset();
      },
      error: () => {
        this.errorMessage = 'Não foi possível cadastrar o quarto.';
      }
    });
  }
}
