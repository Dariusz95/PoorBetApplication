import { TemplateRef } from '@angular/core';

export type PbSortDirection = 'asc' | 'desc';

export interface PbSortState<TData> {
  key: keyof TData;
  direction: PbSortDirection;
}

export interface PbCellContext<TData, TValue> {
  $implicit: TValue;
  row: TData;
  index: number;
}

export interface PbColumnDef<TData, K extends keyof TData = keyof TData> {
  key: K;
  header: string;
  sortable?: boolean;
  align?: 'left' | 'center' | 'right';
  width?: string;
  formatter?: (value: TData[K], row: TData) => string;
  cellTemplate?: TemplateRef<PbCellContext<TData, TData[K]>>;
}
