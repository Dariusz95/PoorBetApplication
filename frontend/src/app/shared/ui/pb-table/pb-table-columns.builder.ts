import { PbColumnDef } from './pb-table.types';

export class PbTableColumnsBuilder<TData> {
  private readonly columns: PbColumnDef<TData>[] = [];

  add<K extends keyof TData>(
    key: K,
    config: Omit<PbColumnDef<TData, K>, 'key'>,
  ): this {
    this.columns.push({ key, ...config } as PbColumnDef<TData>);
    return this;
  }

  build(): PbColumnDef<TData>[] {
    return this.columns;
  }
}

export function tableColumns<TData>(): PbTableColumnsBuilder<TData> {
  return new PbTableColumnsBuilder<TData>();
}
