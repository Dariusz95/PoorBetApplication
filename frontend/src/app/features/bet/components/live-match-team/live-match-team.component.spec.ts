import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamService } from '@features/bet/services/team.service';
import { ShortTeamInfo } from '@features/bet/types/match.types';
import { Uuid } from '@shared/types/uuid.type';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { LiveMatchTeamComponent } from './live-match-team.component';

describe('LiveMatchTeamComponent', () => {
  let fixture: ComponentFixture<LiveMatchTeamComponent>;

  const mockTeam: ShortTeamInfo = {
    id: '123e4567-e89b-12d3-a456-426614174001' as Uuid,
    name: 'Test FC',
    img: 'https://example.com/logo.png',
  };

  const teamServiceMock = {
    getDetails: vi.fn(() => of(mockTeam)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LiveMatchTeamComponent],
      providers: [{ provide: TeamService, useValue: teamServiceMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(LiveMatchTeamComponent);
    fixture.componentRef.setInput('teamId', mockTeam.id);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should fetch team details for the given teamId', () => {
    expect(teamServiceMock.getDetails).toHaveBeenCalledWith(mockTeam.id);
  });

  it('should render the team name and logo', () => {
    const img: HTMLImageElement = fixture.nativeElement.querySelector(
      '.live-match-team__logo',
    );

    expect(fixture.nativeElement.textContent).toContain(mockTeam.name);
    expect(img.src).toBe(mockTeam.img);
  });

  it('should fall back to the team initial when the logo fails to load', () => {
    const img: HTMLImageElement = fixture.nativeElement.querySelector(
      '.live-match-team__logo',
    );

    img.dispatchEvent(new Event('error'));
    fixture.detectChanges();

    const fallback = fixture.nativeElement.querySelector(
      '.live-match-team__logo-fallback',
    );
    expect(fallback?.textContent).toBe(mockTeam.name.charAt(0));
  });
});
