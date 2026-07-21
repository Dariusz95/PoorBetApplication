import { NgTemplateOutlet } from '@angular/common';
import {
  Component,
  computed,
  input,
  linkedSignal,
  output,
} from '@angular/core';
import { TranslocoPipe } from '@jsverse/transloco';
import { PbColumnDef, PbSortDirection, PbSortState } from './pb-table.types';

@Component({
  selector: 'pb-table',
  standalone: true,
  imports: [NgTemplateOutlet, TranslocoPipe],
  templateUrl: './pb-table.component.html',
  styleUrl: './pb-table.component.scss',
})
export class PbTableComponent<TData> {
  data = input<TData[]>([]);
  columns = input<PbColumnDef<TData>[]>([]);
  sortState = input<PbSortState<TData> | null>(null);
  trackByFn = input<(index: number, row: TData) => unknown>(
    (index: number) => index,
  );

  pageSize = input<number | null>(null);

  page = linkedSignal<TData[], number>({
    source: this.data,
    computation: () => 1,
  });

  totalPages = computed(() => {
    const size = this.pageSize();
    if (!size) {
      return 1;
    }
    return Math.max(1, Math.ceil(this.data().length / size));
  });

  pagedData = computed(() => {
    const size = this.pageSize();
    if (!size) {
      return this.data();
    }
    const start = (this.page() - 1) * size;
    return this.data().slice(start, start + size);
  });

  sortChange = output<PbSortState<TData>>();

  onHeaderClick(column: PbColumnDef<TData>): void {
    if (!column.sortable) {
      return;
    }

    const current = this.sortState();
    const direction: PbSortDirection =
      current?.key === column.key && current.direction === 'asc'
        ? 'desc'
        : 'asc';

    this.sortChange.emit({ key: column.key, direction });
  }

  cellValue(row: TData, column: PbColumnDef<TData>): string {
    const value = row[column.key];
    return column.formatter ? column.formatter(value, row) : String(value);
  }

  trackByRow = (index: number, row: TData): unknown =>
    this.trackByFn()(index, row);

  goToPage(target: number): void {
    this.page.set(Math.min(Math.max(1, target), this.totalPages()));
  }
}
