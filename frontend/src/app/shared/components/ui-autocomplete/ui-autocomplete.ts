import {
  Component, input, model, computed, signal,
  ChangeDetectionStrategy, output, ElementRef, inject, HostListener,
} from '@angular/core';

export interface AutocompleteOption {
  value: string;
  label: string;
}

@Component({
  selector: 'app-ui-autocomplete',
  standalone: true,
  templateUrl: './ui-autocomplete.html',
  styleUrl: './ui-autocomplete.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UiAutocomplete {
  private readonly elRef = inject(ElementRef);

  /** The selected value (two-way binding) */
  value = model<string>('');

  /** Full list of options to filter from */
  options = input<AutocompleteOption[]>([]);
  placeholder = input<string>('Type to search...');
  label = input<string>('');
  errorText = input<string>('');
  required = input<boolean>(false);
  disabled = input<boolean>(false);
  id = input<string>(`autocomplete-${Math.random().toString(36).substr(2, 9)}`);

  selected = output<AutocompleteOption>();

  /** Internal search query */
  readonly query = signal('');
  readonly isOpen = signal(false);
  readonly highlightIndex = signal(-1);

  protected readonly hasError = computed(() => !!this.errorText());

  protected readonly inputClasses = computed(() => {
    const classes = ['form-input', 'autocomplete-input'];
    if (this.hasError()) classes.push('error');
    return classes.join(' ');
  });

  /** Filtered options based on current query */
  protected readonly filteredOptions = computed(() => {
    const q = this.query().toLowerCase().trim();
    const all = this.options();
    if (!q) return all.slice(0, 50); // show first 50 when empty
    return all.filter(o =>
      o.label.toLowerCase().includes(q) || o.value.toLowerCase().includes(q)
    ).slice(0, 50);
  });

  /** Check if current query would create a new custom option */
  protected readonly isCustomValue = computed(() => {
    const q = this.query().trim();
    if (!q) return false;
    return !this.options().some(o =>
      o.value.toLowerCase() === q.toLowerCase() ||
      o.label.toLowerCase() === q.toLowerCase()
    );
  });

  /** Display value — show label of selected value, or the query */
  protected readonly displayValue = computed(() => {
    const v = this.value();
    if (v) {
      const match = this.options().find(o => o.value === v);
      return match?.label ?? v;
    }
    return this.query();
  });

  handleInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    const inputValue = target.value;
    this.query.set(inputValue);
    this.value.set(inputValue); // Set value to allow custom entries
    this.isOpen.set(true);
    this.highlightIndex.set(-1);
  }

  handleFocus(): void {
    this.isOpen.set(true);
  }

  handleBlur(): void {
    // Small delay to allow option click to register
    setTimeout(() => {
      this.isOpen.set(false);
      // Ensure value is set to current query if no selection was made
      const currentQuery = this.query();
      if (currentQuery && !this.value()) {
        this.value.set(currentQuery);
      }
    }, 150);
  }

  selectOption(option: AutocompleteOption): void {
    this.value.set(option.value);
    this.query.set(option.label);
    this.isOpen.set(false);
    this.highlightIndex.set(-1);
    this.selected.emit(option);
  }

  selectCustomValue(): void {
    const customValue = this.query().trim();
    if (customValue) {
      this.value.set(customValue);
      this.isOpen.set(false);
      this.highlightIndex.set(-1);
      // Emit as a new option
      this.selected.emit({ value: customValue, label: customValue });
    }
  }

  handleKeydown(event: KeyboardEvent): void {
    const opts = this.filteredOptions();
    const totalOptions = opts.length + (this.isCustomValue() && this.query().trim() ? 1 : 0);

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        this.highlightIndex.update(i => Math.min(i + 1, totalOptions - 1));
        break;
      case 'ArrowUp':
        event.preventDefault();
        this.highlightIndex.update(i => Math.max(i - 1, 0));
        break;
      case 'Enter':
        event.preventDefault();
        const idx = this.highlightIndex();
        if (idx >= 0 && idx < opts.length) {
          this.selectOption(opts[idx]);
        } else if (idx === opts.length && this.isCustomValue()) {
          // Select the "Create new" option
          this.selectCustomValue();
        } else {
          // Accept custom value if no option is highlighted
          const currentQuery = this.query();
          if (currentQuery.trim()) {
            this.selectCustomValue();
          }
        }
        break;
      case 'Tab':
        // Accept current value and close dropdown on Tab
        const currentQuery = this.query();
        if (currentQuery.trim()) {
          this.value.set(currentQuery.trim());
        }
        this.isOpen.set(false);
        break;
      case 'Escape':
        this.isOpen.set(false);
        break;
    }
  }

  /** Close dropdown when clicking outside */
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elRef.nativeElement.contains(event.target)) {
      this.isOpen.set(false);
    }
  }
}

