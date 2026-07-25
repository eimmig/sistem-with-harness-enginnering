import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { GuestService } from '../guest.service';
import { Guest } from '../guest.model';

@Component({
  selector: 'app-guest-form',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './guest-form.component.html',
  styleUrl: './guest-form.component.scss'
})
export class GuestFormComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly guestService = inject(GuestService);

  @Output() guestCreated = new EventEmitter<Guest>();

  errorMessage: string | null = null;

  form = this.formBuilder.nonNullable.group({
    name: ['', Validators.required],
    document: ['', Validators.required],
    phone: ['', Validators.required]
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = null;
    this.guestService.create(this.form.getRawValue()).subscribe({
      next: (guest) => {
        this.guestCreated.emit(guest);
        this.form.reset();
      },
      error: () => {
        this.errorMessage = 'Não foi possível cadastrar o hóspede. Verifique os dados e tente novamente.';
      }
    });
  }
}
