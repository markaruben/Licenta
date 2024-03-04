export function info(
  enqueueSnackbar: (message: string, options?: any) => void,
  message: string
): void {
  enqueueSnackbar(message, { autoHideDuration: 3000, variant: "info" });
}

export function success(
  enqueueSnackbar: (message: string, options?: any) => void,
  message: string
): void {
  enqueueSnackbar(message, { autoHideDuration: 3000, variant: "success" });
}

export function error(
  enqueueSnackbar: (message: string, options?: any) => void,
  message: string
): void {
  enqueueSnackbar(message, { autoHideDuration: 3000, variant: "error" });
}
