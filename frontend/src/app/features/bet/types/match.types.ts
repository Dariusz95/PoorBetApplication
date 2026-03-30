import { Uuid } from '@shared/types/uuid.type';

export interface LiveMatchEvent {
  id: string;
  minute: number;
  homeTeamId: string;
  awayTeamId: string;
  homeScore: number;
  awayScore: number;
  eventType: MatchEventType;
  eventData: null | string;
}

export enum MatchEventType {
  MatchStarted = 'MATCH_STARTED',
  Live = 'LIVE',
  MatchEnded = 'MATCH_ENDED',
  Heartbeat = 'HEARTBEAT',
  MatchPoolFinished = 'MATCH_POOL_FINISHED',
}

export interface OddsDto {
  id: string;
  homeWin: number;
  draw: number;
  awayWin: number;
}

export interface MatchDto {
  matchId: string;
  homeTeamId: string;
  awayTeamId: string;
  odds: OddsDto;
}

export interface PoolMatch {
  id: Uuid;
  status: string;
  scheduledStartTime: string;
  matches: MatchDto[];
}

export interface ShortTeamInfo {
  id: string;
  name: string;
}
