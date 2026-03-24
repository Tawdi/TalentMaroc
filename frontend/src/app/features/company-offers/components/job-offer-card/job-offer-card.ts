import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OfferResponse, OFFER_CONTRACT_TYPE_LABELS } from '../../../../core/models/company-offers.model';
import { UiBadge, UiButton, IconComponent } from '../../../../shared';

@Component({
  selector: 'app-job-offer-card',
  standalone: true,
  imports: [CommonModule, UiBadge, UiButton, IconComponent],
  templateUrl: './job-offer-card.html',
  styleUrl: './job-offer-card.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class JobOfferCardComponent {
  @Input({ required: true }) offer!: OfferResponse;
  @Input() isSaved = false;
  @Input() saving = false;
  @Input() enableSave = true;
  @Output() view = new EventEmitter<OfferResponse>();
  @Output() toggleSave = new EventEmitter<OfferResponse>();

  readonly contractLabels = OFFER_CONTRACT_TYPE_LABELS;

  handleView(event?: Event): void {
    event?.stopPropagation();
    this.view.emit(this.offer);
  }

  handleToggleSave(event: Event): void {
    event.stopPropagation();
    if (!this.enableSave || this.saving) return;
    this.toggleSave.emit(this.offer);
  }

  get descriptionPreview(): string {
    const desc = this.offer.description || '';
    return desc.length > 180 ? `${desc.slice(0, 180)}...` : desc;
  }

  timeAgo(dateStr?: string): string {
    if (!dateStr) return 'Recently added';
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    if (diffDays <= 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`;
    return `${Math.floor(diffDays / 30)} months ago`;
  }
}
