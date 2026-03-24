import { Component, inject, OnInit, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { UiCard, UiBadge, UiButton, IconComponent, UiConfirmDialog, ConfirmDialogData, UiAlert } from '../../../../../shared';
import { AdminActions } from '../../store/admin.actions';
import * as AdminSelectors from '../../store/admin.selectors';

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, UiCard, UiBadge, UiButton, IconComponent, UiConfirmDialog, UiAlert],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminUsersComponent implements OnInit {
  private store = inject(Store);

  users = this.store.selectSignal(AdminSelectors.selectUsers);
  totalElements = this.store.selectSignal(AdminSelectors.selectUsersTotalElements);
  page = this.store.selectSignal(AdminSelectors.selectUsersPage);
  size = this.store.selectSignal(AdminSelectors.selectUsersSize);
  loading = this.store.selectSignal(AdminSelectors.selectUsersLoading);
  error = this.store.selectSignal(AdminSelectors.selectUsersError);

  // Confirm dialog
  showConfirmDialog = signal(false);
  confirmTarget = signal<string | null>(null);
  confirmDialogData = signal<ConfirmDialogData>({
    title: '',
    message: '',
    confirmText: 'Delete',
    cancelText: 'Cancel',
    variant: 'danger',
    icon: 'trash',
  });

  ngOnInit() {
    this.loadUsers(0);
  }

  loadUsers(page: number) {
    this.store.dispatch(AdminActions.loadUsers({ page }));
  }

  deleteUser(userId: string) {
    this.confirmTarget.set(userId);
    this.confirmDialogData.set({
      title: 'Delete User',
      message: 'Are you sure you want to delete this user? This action cannot be undone.',
      confirmText: 'Delete',
      cancelText: 'Cancel',
      variant: 'danger',
      icon: 'trash',
    });
    this.showConfirmDialog.set(true);
  }

  confirmDelete() {
    const userId = this.confirmTarget();
    if (userId) {
      this.store.dispatch(AdminActions.deleteUser({ userId }));
      this.showConfirmDialog.set(false);
      this.confirmTarget.set(null);
    }
  }

  cancelConfirm() {
    this.showConfirmDialog.set(false);
    this.confirmTarget.set(null);
  }

  clearError() {
    this.store.dispatch(AdminActions.clearError());
  }

  nextPage() {
    if ((this.page() + 1) * this.size() < this.totalElements()) {
      this.loadUsers(this.page() + 1);
    }
  }

  prevPage() {
    if (this.page() > 0) {
      this.loadUsers(this.page() - 1);
    }
  }
}

