import { Component, Input, OnChanges, OnInit, SimpleChanges, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { GuestService } from '../guest.service';
import { Guest } from '../guest.model';

@Component({
  selector: 'app-guest-search',
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatTableModule],
  templateUrl: './guest-search.component.html',
  styleUrl: './guest-search.component.scss'
})
export class GuestSearchComponent implements OnInit, OnChanges {
  private readonly formBuilder = inject(FormBuilder);
  private readonly guestService = inject(GuestService);

  @Input() refreshSignal: unknown;

  readonly displayedColumns = ['name', 'document', 'phone'];
  guests: Guest[] = [];
  searched = false;

  form = this.formBuilder.nonNullable.group({
    name: [''],
    document: [''],
    phone: ['']
  });

  ngOnInit(): void {
    this.search();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['refreshSignal']?.firstChange) {
      this.search();
    }
  }

  search(): void {
    this.guestService.search(this.form.getRawValue()).subscribe((guests) => {
      this.guests = guests;
      this.searched = true;
    });
  }
}
