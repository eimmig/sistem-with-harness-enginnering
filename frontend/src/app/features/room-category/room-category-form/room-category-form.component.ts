import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RoomCategoryService } from '../room-category.service';
import { RoomCategory } from '../room-category.model';

@Component({
  selector: 'app-room-category-form',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './room-category-form.component.html',
  styleUrl: './room-category-form.component.scss'
})
export class RoomCategoryFormComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly roomCategoryService = inject(RoomCategoryService);

  @Output() categoryCreated = new EventEmitter<RoomCategory>();

  errorMessage: string | null = null;

  form = this.formBuilder.nonNullable.group({
    name: ['', Validators.required]
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = null;
    this.roomCategoryService.create(this.form.getRawValue()).subscribe({
      next: (category) => {
        this.categoryCreated.emit(category);
        this.form.reset();
      },
      error: () => {
        this.errorMessage = 'Não foi possível cadastrar a categoria.';
      }
    });
  }
}
