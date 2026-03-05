import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';
import {UiButton} from '../../shared';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, UiButton, UiButton],
  template: `
    <div class="home-page">
      <!-- Hero Section -->
      <section class="hero">
        <div class="hero-content animate-fade-in">
          <h1 class="hero-title">
            Find Your Dream Job in
            <span class="highlight">Morocco</span>
          </h1>
          <p class="hero-subtitle">
            Connect with top employers and discover thousands of job opportunities
            across Morocco. Your next career move starts here.
          </p>
          <div class="hero-search">
            <input type="text" placeholder="Job title, keywords, or company" class="search-input" />
            <input type="text" placeholder="City or region" class="search-input" />
            <app-ui-button variant="primary" size="lg">Search Jobs</app-ui-button>
          </div>
          <div class="hero-stats">
            <div class="stat">
              <span class="stat-number">10,000+</span>
              <span class="stat-label">Active Jobs</span>
            </div>
            <div class="stat">
              <span class="stat-number">5,000+</span>
              <span class="stat-label">Companies</span>
            </div>
            <div class="stat">
              <span class="stat-number">50,000+</span>
              <span class="stat-label">Job Seekers</span>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="cta-section">
        <div class="cta-content">
          <h2>Ready to get started?</h2>
          <p>Join TalentMaroc today and take the next step in your career</p>
          <div class="cta-buttons">
            <a routerLink="/auth/register" class="btn btn-primary btn-lg">Create Account</a>
            <a routerLink="/jobs" class="btn btn-outline btn-lg">Browse Jobs</a>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .home-page {
      min-height: 100%;
    }

    .hero {
      padding: 4rem 1.5rem 6rem;
      text-align: center;
      background: linear-gradient(135deg, var(--background) 0%, var(--background-secondary) 100%);
    }

    .hero-content {
      max-width: 800px;
      margin: 0 auto;
    }

    .hero-title {
      font-size: 3rem;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 1.5rem;
      line-height: 1.2;
    }

    .highlight {
      color: var(--primary);
    }

    .hero-subtitle {
      font-size: 1.25rem;
      color: var(--text-secondary);
      margin-bottom: 2.5rem;
      line-height: 1.6;
    }

    .hero-search {
      display: flex;
      gap: 0.75rem;
      max-width: 700px;
      margin: 0 auto 3rem;
      padding: 0.5rem;
      background-color: var(--card-bg);
      border-radius: 0.75rem;
      box-shadow: 0 4px 20px var(--card-shadow);
    }

    .search-input {
      flex: 1;
      padding: 0.875rem 1rem;
      border: none;
      background: none;
      font-size: 1rem;
      color: var(--text-primary);
      outline: none;
    }

    .search-input::placeholder {
      color: var(--text-muted);
    }

    .hero-stats {
      display: flex;
      justify-content: center;
      gap: 4rem;
    }

    .stat {
      display: flex;
      flex-direction: column;
    }

    .stat-number {
      font-size: 2rem;
      font-weight: 700;
      color: var(--primary);
    }

    .stat-label {
      font-size: 0.875rem;
      color: var(--text-muted);
    }

    .cta-section {
      padding: 4rem 1.5rem;
      background-color: var(--primary);
      text-align: center;
    }

    .cta-content {
      max-width: 600px;
      margin: 0 auto;
    }

    .cta-content h2 {
      font-size: 2rem;
      font-weight: 700;
      color: white;
      margin-bottom: 0.75rem;
    }

    .cta-content p {
      font-size: 1.125rem;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 2rem;
    }

    .cta-buttons {
      display: flex;
      justify-content: center;
      gap: 1rem;
    }

    .cta-buttons .btn-primary {
      background-color: white;
      color: var(--primary);
    }

    .cta-buttons .btn-outline {
      border-color: white;
      color: white;
    }

    .cta-buttons .btn-outline:hover {
      background-color: white;
      color: var(--primary);
    }

    @media (max-width: 768px) {
      .hero-title {
        font-size: 2rem;
      }

      .hero-search {
        flex-direction: column;
      }

      .hero-stats {
        gap: 2rem;
      }

      .stat-number {
        font-size: 1.5rem;
      }

      .cta-buttons {
        flex-direction: column;
      }
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HomeComponent {}

