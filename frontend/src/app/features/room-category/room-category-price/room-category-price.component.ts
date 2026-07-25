import { Component, Input, OnChanges, OnInit, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { RoomCategoryService } from '../room-category.service';
import { DAYS_OF_WEEK, DAY_OF_WEEK_LABELS, DayOfWeek, RoomCategory } from '../room-category.model';

@Component({
  selector: 'app-room-category-price',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule],
  templateUrl: './room-category-price.component.html',
  styleUrl: './room-category-price.component.scss'
})
export class RoomCategoryPriceComponent implements OnInit, OnChanges {
  private readonly formBuilder = inject(FormBuilder);
  private readonly roomCategoryService = inject(RoomCategoryService);

  @Input() refreshSignal: unknown;

  readonly days = DAYS_OF_WEEK;
  readonly dayLabels = DAY_OF_WEEK_LABELS;

  categories: RoomCategory[] = [];
  selectedCategoryId: number | null = null;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  form: FormGroup<Record<DayOfWeek, FormControl<number | null>>> = this.formBuilder.group(
    DAYS_OF_WEEK.reduce(
      (controls, day) => {
        controls[day] = this.formBuilder.control<number | null>(null, [Validators.required, Validators.min(0.01)]);
        return controls;
      },
      {} as Record<DayOfWeek, FormControl<number | null>>
    )
  );

  ngOnInit(): void {
    this.loadCategories();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['refreshSignal']?.firstChange) {
      this.loadCategories();
    }
  }

  loadCategories(): void {
    this.roomCategoryService.list().subscribe((categories) => {
      this.categories = categories;
    });
  }

  onCategorySelected(id: number): void {
    this.selectedCategoryId = id;
    this.successMessage = null;
    this.errorMessage = null;
    const category = this.categories.find((c) => c.id === id);
    for (const day of DAYS_OF_WEEK) {
      this.form.controls[day].setValue(category?.prices[day] ?? null);
    }
  }

  submit(): void {
    if (this.selectedCategoryId === null || this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.successMessage = null;
    this.errorMessage = null;
    const prices = this.form.getRawValue() as Record<DayOfWeek, number>;
    this.roomCategoryService.updatePrices(this.selectedCategoryId, { prices }).subscribe({
      next: () => {
        this.successMessage = 'Preços atualizados com sucesso.';
        this.loadCategories();
      },
      error: () => {
        this.errorMessage = 'Não foi possível atualizar os preços. Verifique se todos os dias estão preenchidos com valores positivos.';
      }
    });
  }
}
