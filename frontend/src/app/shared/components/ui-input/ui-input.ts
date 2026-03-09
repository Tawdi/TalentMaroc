import { Component, input, model, computed, ChangeDetectionStrategy, output } from '@angular/core';

export type InputType = 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'search';

@Component({
  selector: 'app-ui-input',
  standalone: true,
  imports: [],
  templateUrl: './ui-input.html',
  styleUrl: './ui-input.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiInput {
  // Two-way binding with model signals
  value = model<string>('');

  // Input properties
  type = input<InputType>('text');
  placeholder = input<string>('');
  label = input<string>('');
  helperText = input<string>('');
  errorText = input<string>('');
  required = input<boolean>(false);
  disabled = input<boolean>(false);
  readonly = input<boolean>(false);
  id = input<string>(`input-${Math.random().toString(36).substr(2, 9)}`);

  // Outputs
  focused = output<FocusEvent>();
  blurred = output<FocusEvent>();

  // Computed states
  protected readonly hasError = computed(() => !!this.errorText());

  protected readonly inputClasses = computed(() => {
    const classes = ['form-input'];
    if (this.hasError()) {
      classes.push('error');
    }
    return classes.join(' ');
  });

  handleInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.value.set(target.value);
  }

  handleFocus(event: FocusEvent): void {
    this.focused.emit(event);
  }

  handleBlur(event: FocusEvent): void {
    this.blurred.emit(event);
  }
}
