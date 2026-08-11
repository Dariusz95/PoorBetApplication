import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ToastService } from '@shared/services/toast.service';
import { Uuid } from '@shared/types/uuid.type';
import { getTranslocoModule } from '@shared/utils/get-transloco-module';
import { of, Subject } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { TeamService } from '../../../../services/team.service';
import { Team } from '../../../../types/team';
import { TeamViewComponent } from './team-view.component';

describe('TeamViewComponent', () => {
  let component: TeamViewComponent;
  let fixture: ComponentFixture<TeamViewComponent>;
  let teamService: { increasePower: ReturnType<typeof vi.fn> };
  let toastService: {
    success: ReturnType<typeof vi.fn>;
    error: ReturnType<typeof vi.fn>;
  };

  const team: Team = {
    id: 'team-1' as Uuid,
    name: 'Lechia Gdańsk',
    logo: null,
    attackPower: 40,
    defencePower: 40,
  };

  beforeEach(async () => {
    teamService = { increasePower: vi.fn() };
    toastService = { success: vi.fn(), error: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [TeamViewComponent, getTranslocoModule()],
      providers: [
        { provide: TeamService, useValue: teamService },
        { provide: ToastService, useValue: toastService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TeamViewComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('team', team);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('increasePower', () => {
    it('should call teamService.increasePower, emit teamUpdated and show a success toast', () => {
      const updatedTeam: Team = { ...team, attackPower: 41 };
      teamService.increasePower.mockReturnValue(of(updatedTeam));

      let emitted: Team | undefined;
      component.teamUpdated.subscribe((value) => (emitted = value));

      component.increasePower('ATTACK');

      expect(teamService.increasePower).toHaveBeenCalledWith('ATTACK');
      expect(emitted).toEqual(updatedTeam);
      expect(toastService.success).toHaveBeenCalled();
      expect(component.increasingPower()).toBeNull();
    });

    it('should ignore a second click while a request is in flight', () => {
      const subject = new Subject<Team>();
      teamService.increasePower.mockReturnValue(subject);

      component.increasePower('ATTACK');
      component.increasePower('DEFENCE');

      expect(teamService.increasePower).toHaveBeenCalledTimes(1);
      expect(component.increasingPower()).toBe('ATTACK');
    });
  });
});
