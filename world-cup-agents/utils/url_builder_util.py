import os

import requests

base_url = os.getenv("BACKEND_API_URL", "http://localhost:8083")


def tool_caller(url_params, **params):
    try:
        url = f"{base_url}/{url_params}?"
        for param_key, param_val in params.items():
            url += param_key
            url += "="
            url += str(param_val)
            url += "&"
        response = requests.get(url[0 : len(url) - 1], timeout=60)
        if response.status_code == 200:
            return response.json()
        if response.status_code == 204:
            return {"error": "No news cached yet for this request"}
        return {"error": f"Service returned {response.status_code}"}
    except Exception as e:
        return {"error": str(e)}
