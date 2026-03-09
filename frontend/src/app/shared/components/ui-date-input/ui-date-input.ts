import { Component, input, model, computed, ChangeDetectionStrategy, output } from '@angular/core';

@Component({
  selector: 'app-ui-date-input',
  standalone: true,
  templateUrl: './ui-date-input.html',
  styleUrl: './ui-date-input.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiDateInput {
  value = model<string>('');

  label = input<string>('');
  errorText = input<string>('');
  helperText = input<string>('');
  required = input<boolean>(false);
  disabled = input<boolean>(false);
  min = input<string>('');
  max = input<string>('');
  id = input<string>(`date-${Math.random().toString(36).substr(2, 9)}`);

  changed = output<string>();

  protected readonly hasError = computed(() => !!this.errorText());

  protected readonly inputClasses = computed(() => {
    const classes = ['form-input', 'date-input'];
    if (this.hasError()) classes.push('error');
    return classes.join(' ');
  });

  handleInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.value.set(target.value);
    this.changed.emit(target.value);
  }
}

